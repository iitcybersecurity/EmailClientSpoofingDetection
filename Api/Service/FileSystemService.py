import os

current_path = os.getcwd()


def create_user_folders(email_address):
    user_folder_path = current_path + '/Storage/' + email_address
    if not os.path.exists(user_folder_path):
        models_path = user_folder_path + '/Models'
        emails_path = user_folder_path + '/Emails'
        os.makedirs(user_folder_path)
        os.makedirs(models_path)
        os.makedirs(emails_path)


def store_email(recipient_address, sender_address, email_data, message_id):
    user_emails_folder_path = current_path + '/Storage/' + recipient_address + '/Emails/' + sender_address
    if not os.path.exists(user_emails_folder_path):
        os.makedirs(user_emails_folder_path)

    f = open('%s/%s.eml' % (user_emails_folder_path, message_id), 'wb')
    f.write(email_data)
    f.close()
