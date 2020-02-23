from flask import request, Response
from flask_restplus import Resource
from datetime import datetime
from Endpoints.Utils import JsonTransformer
from Restplus import api
from DTO.LoginDto import LoginDto
from Service.DBService import get_user, insert_to_train_email, get_last_timestamp_by_recipient, email_exists, insert_to_test_email
from Service.FileSystemService import create_user_folders, store_email
from Service.MailServerConnectionService import download_emails


ns_endpoint = api.namespace(
    'login', description='login related operations')
_login = LoginDto.login


@ns_endpoint.route("", methods=['POST'])
class apiEndpoint(Resource):

    # Rest Api used to make login, if necessary create user and download emails
    @api.doc('Rest Api used to make login, if necessary create user and download emails')
    @api.expect(_login, validate=True)
    def post(self):
        # Get data from request
        login_data = request.get_json()
        # Get user email address from request
        email_address = login_data['email_address']
        # Get date start from request
        date_start = datetime.strptime(
            login_data['date_start'], "%Y-%m-%dT%H:%M:%S.%f%z").date()
        # Verify if the user already exists on db
        existing_user = get_user(email_address)
        # If not exists create file system folders to save downloaded emails and neural network models, else get last timestamp on db
        if(not existing_user):
            create_user_folders(email_address)
        else:
            last_timestamp = get_last_timestamp_by_recipient(email_address)
            # Compare date_start of request and last timestamp and get the oldest one
            if(last_timestamp):
                date_compare_list = [date_start, last_timestamp]
                date_start = min(date_compare_list)
        # Download email starting from the oldest date between date_start from request and last_timestamp(if it exists)
        download_response = download_emails(
            email_address, login_data['password'], login_data['email_port'], login_data['email_imap'], date_start)
        # `response` is keyed by message id and contains parsed, converted response items.
        email_list = []
        for message_id, data in download_response.items():
            envelope = data[b'ENVELOPE']
            sender = envelope.from_[0]
            sender_address = '{mailbox}@{host}'.format(
                mailbox=sender.mailbox.decode(), host=sender.host.decode())

            # TODO: controllo su validità formato indirizzo

            timestamp = envelope.date
            subject = envelope.subject.decode()
            body = data[b'BODY[TEXT]'].decode(errors="ignore")
            # print('{id}: {size} bytes, flags={flags}, sender={sender}, subject={subject}, date={date}'.format(
            #     id=message_id,
            #     size=data[b'RFC822.SIZE'],
            #     flags=data[b'FLAGS'],
            #     sender=sender_address,
            #     subject=subject,
            #     date=timestamp))

            envelope_dic = {"Sender": sender_address, "Subject": subject, "Timestamp": timestamp, "Body": body}
            email_list.append(envelope_dic)
            # Un'email potrebbe già esistere anche per utenti "nuovi" se ci sono due email arrivate dallo stesso mittente allo stesso secondo e
            # una è già stata inserita ai passi prima.
            # E' successo con due email di verifica di sicurezza da parte di Microsoft.
            if(not email_exists(email_address, sender_address, timestamp)):
                # Salva la email su file system nella cartella dell'utente ricevente
                store_email(email_address, sender_address, data[b'RFC822'], timestamp)

                # If user not exists insert email into db and mark it to train
                if(not existing_user):
                    insert_to_train_email(
                        email_address, sender_address, timestamp)
                else:
                    insert_to_test_email(
                        email_address, sender_address, timestamp)

        transformToJson = JsonTransformer()
        json_result = transformToJson.transform(email_list)

        return Response(json_result, status=200, mimetype='application/json')
