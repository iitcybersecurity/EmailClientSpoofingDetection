from imapclient import IMAPClient
from datetime import datetime
import email


def download_emails(access_info):
    address = access_info['email_address']
    password = access_info['password']
    email_port = access_info['email_port']
    email_imap = access_info['email_imap']
    date_start = datetime.strptime(access_info['date_start'], "%Y-%m-%dT%H:%M:%S.%f%z").date() 

    # print(address + '-' + password + '-' + str(email_port) + '-' + email_imap + '-' + str(date_start))

    ssl = email_port == 993
    server = IMAPClient(email_imap, use_uid=True, ssl=ssl)
    server.login(address, password)
    server.select_folder('INBOX')

    messages = server.search([u'SINCE', date_start])

    # fetch selectors are passed as a simple list of strings.
    response = server.fetch(messages, ['FLAGS', 'RFC822.SIZE', 'RFC822', 'ENVELOPE', 'BODY[TEXT]'])

    # `response` is keyed by message id and contains parsed,
    # converted response items.
    for message_id, data in response.items():
        envelope = data[b'ENVELOPE']
        # parsedEmail = email.message_from_string(data['RFC822'])
        # body = email.message_from_string(data['BODY[TEXT]'])
        # parsedBody = parsedEmail.get_payload(0)
        print('{id}: {size} bytes, flags={flags}, sender={sender}, subject={subject}, date={date}'.format(
            id=message_id,
            size=data[b'RFC822.SIZE'],
            flags=data[b'FLAGS'],
            sender=envelope.sender,
            subject=envelope.subject.decode(),
            date=envelope.date))

    server.logout()
