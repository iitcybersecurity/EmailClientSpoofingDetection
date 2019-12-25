import os


def create_user_folders(email_address):
    current_path = os.getcwd()
    user_folder_path = current_path + '\\Storage\\' + email_address
    if not os.path.exists(user_folder_path):
        models_path = user_folder_path + '\\Models'
        emails_path = user_folder_path + '\\Emails'
        os.makedirs(user_folder_path)
        os.makedirs(models_path)
        os.makedirs(emails_path)
