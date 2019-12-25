from imapclient import IMAPClient
from datetime import datetime
# import email


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
    response = server.fetch(messages, ['FLAGS', 'RFC822.SIZE', 'RFC822', 'ENVELOPE', 'BODY[HEADER]', 'BODY[TEXT]'])

    server.logout()

    return response
