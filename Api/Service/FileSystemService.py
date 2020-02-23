import os
import shutil

current_path = os.getcwd()


# Creazione cartelle relative al recipient, se non esistono 
def create_user_folders(email_address):
    user_folder_path = current_path + '/Storage/' + email_address
    if not os.path.exists(user_folder_path):
        models_path = user_folder_path + '/Models'
        emails_path = user_folder_path + '/Emails'
        trainings_path = user_folder_path + '/Trainings'
        os.makedirs(user_folder_path)
        os.makedirs(models_path)
        os.makedirs(emails_path)
        os.makedirs(trainings_path)


# Creazione cartelle per i training set, se non esistono
def create_sender_training_folder(recipient_address, sender_address):
    user_training_folder_path = current_path + '/Storage/' + recipient_address + '/Trainings/' + \
        sender_address + "-" + recipient_address
    if not os.path.exists(user_training_folder_path):
        os.makedirs(user_training_folder_path)
        os.makedirs(user_training_folder_path + "/Positive")
        os.makedirs(user_training_folder_path + "/Negative")

    return user_training_folder_path


# Creazione cartella relativa al sender per un dato recipient, se non esiste
def create_sender_folder(recipient_address, sender_address):
    user_emails_folder_path = current_path + '/Storage/' + \
        recipient_address + '/Emails/' + sender_address
    if not os.path.exists(user_emails_folder_path):
        os.makedirs(user_emails_folder_path)

    return user_emails_folder_path


# Memorizzazione delle email nel path 'recipient/sender'.
# Se è un primo accesso (existing_user = false) vuol dire che le email sono settate come 'ToTrain' quindi vengono memorizzate anche 
# nella cartelle relativa al training.
def store_email(recipient_address, sender_address, email_data, timestamp, existing_user):
    user_emails_folder_path = create_sender_folder(recipient_address, sender_address)

    create_file(user_emails_folder_path, timestamp, email_data)

    if(not existing_user):
        user_training_folder_path = create_sender_training_folder(recipient_address, sender_address) + '/Positive'
        create_file(user_training_folder_path, timestamp, email_data)


# Scrittura su file della email nel path dato e usando il timestamp come nome
def create_file(path, timestamp, email_data):
    f = open('%s/%s.eml' %
             (path, str(timestamp).replace(" ", "_").replace(":", "")), 'wb')
    f.write(email_data)
    f.close()


# Copia i file relativi alla lista di email data nel path dato 
def copy_emails_to_path(email_list, dst_path):
    for email in email_list:
        file_path = current_path + '/Storage/' + \
            email.Recipient + '/Emails/' + email.Sender
        file_name = str(email.Timestamp).replace(" ", "_").replace(":", "") + '.eml'
        shutil.copy(file_path + '/' + file_name, dst_path + '/' + file_name)
