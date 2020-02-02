from imapclient import IMAPClient
from datetime import timedelta


def download_emails(address, password, email_port, email_imap, date_start):
    # print(address + '-' + password + '-' + str(email_port) + '-' + email_imap + '-' + str(date_start))

    ssl = email_port == 993
    server = IMAPClient(email_imap, use_uid=True, ssl=ssl)
    server.login(address, password)
    server.select_folder('INBOX')

    messages = server.search([u'SINCE', date_start - timedelta(days=1)])

    # fetch selectors are passed as a simple list of strings.
    response = server.fetch(messages, ('FLAGS', 'RFC822.SIZE', 'RFC822', 'ENVELOPE', 'BODY[TEXT]'))

    server.logout()

    return response
