from flask import request
from flask_restplus import Resource
import email
from Restplus import api
from DTO.LoginDto import LoginDto
from Service.DBService import get_user, insert_to_train_email
from Service.FileSystemService import create_user_folders
from Service.MailServerConnectionService import download_emails


ns_endpoint = api.namespace(
    'api/login', description='login related operations')
_login = LoginDto.login


@ns_endpoint.route("/", methods=['POST'])
class apiEndpoint(Resource):

    # Rest Api used to make login, if necessary create user and download emails
    @api.doc('Rest Api used to make login, if necessary create user and download emails')
    @api.expect(_login, validate=True)
    def post(self):
        login_data = request.get_json()
        email_address = login_data['email_address']
        existing_user = get_user(email_address)
        response_object = ""
        if(not existing_user):
            create_user_folders(email_address)

        download_response = download_emails(login_data)
        # `response` is keyed by message id and contains parsed, converted response items.
        for message_id, data in download_response.items():
            envelope = data[b'ENVELOPE']
            # parsedEmail = email.message_from_string(data['RFC822'])
            # body = email.message_from_string(data['BODY[TEXT]'])
            # parsedBody = parsedEmail.get_payload(0)
            sender = envelope.from_[0]
            sender_address = '{mailbox}@{host}'.format(mailbox=sender.mailbox.decode(), host=sender.host.decode())
            # TODO: controllo su validità formato indirizzo
            timestamp = envelope.date
            print('{id}: {size} bytes, flags={flags}, sender={sender}, subject={subject}, date={date}'.format(
                id=message_id,
                size=data[b'RFC822.SIZE'],
                flags=data[b'FLAGS'],
                sender=sender_address,
                subject=envelope.subject.decode(),
                date=timestamp))
            insert_to_train_email(email_address, sender_address, timestamp)

        response_object = {
            'status': 'success',
            'message': 'Success',
        }

        return response_object, 200
