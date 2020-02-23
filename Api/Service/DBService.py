from sqlalchemy.orm import sessionmaker
from sqlalchemy import desc
from database import engine
from DBModels.Email import Email
import random


# Get user from db if exists
def get_user(recipient):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == recipient).first()


# Get timestamp of last email on db for a specific recipient
def get_last_timestamp_by_recipient(recipient):
    Session = sessionmaker(bind=engine)
    session = Session()
    last_timestamp = session.query(Email).filter(
        Email.Recipient == recipient).order_by(desc(Email.Timestamp)).first().Timestamp
    if(last_timestamp):
        last_timestamp = last_timestamp.date()
    return last_timestamp


# Get email
def get_email(recipient, sender, timestamp):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).\
        filter(Email.Recipient == recipient).\
        filter(Email.Sender == sender).\
        filter(Email.Timestamp == timestamp).\
        first()


# Get emails by recipient and sender
def get_emails_by_couple(recipient, sender):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == recipient).filter(Email.Sender == sender).all()


# Verify if an email already exists on db
def email_exists(recipient, sender, timestamp):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).\
        filter(Email.Recipient == recipient).\
        filter(Email.Sender == sender).\
        filter(Email.Timestamp == timestamp).\
        scalar() is not None


# Insert email into db and mark it as to train
def insert_to_train_email(recipient, sender, timestamp):
    email = Email(recipient, sender, timestamp, toTrain=True)
    Session = sessionmaker(bind=engine)
    session = Session()
    session.add(email)
    session.commit()


# Insert email into db and mark it as to test
def insert_to_test_email(recipient, sender, timestamp):
    email = Email(recipient, sender, timestamp, toTest=True)
    Session = sessionmaker(bind=engine)
    session = Session()
    session.add(email)
    session.commit()


# Manual verify to list of emails
def report_human_prediction(email_list):
    for email_checked in email_list:
        verify_email(
            email_checked['recipient'], email_checked['sender'], email_checked['timestamp'])
        set_emails_to_train(
            email_checked['recipient'], email_checked['sender'])


# Verify single email
def verify_email(recipient, sender, timestamp):
    Session = sessionmaker(bind=engine)
    session = Session()
    if email_exists(recipient, sender, timestamp):
        session.query(Email).\
            filter(Email.Recipient == recipient).\
            filter(Email.Sender == sender).\
            filter(Email.Timestamp == timestamp).\
            update({Email.Prediction: 1, Email.Verified: True}, synchronize_session=False)
    session.commit()


# Set emails with recipient and sender given and prediction positive toTrain
def set_emails_to_train(recipient, sender):
    Session = sessionmaker(bind=engine)
    session = Session()
    session.query(Email).\
        filter(Email.Recipient == recipient).\
        filter(Email.Sender == sender).\
        filter(Email.Prediction is not None and Email.Prediction > 0).\
        update({Email.ToTrain: True}, synchronize_session=False)
    session.commit()


# Get positive emails number to train
def get_positive_training_emails_number(recipient, sender):
    Session = sessionmaker(bind=engine)
    session = Session()
    to_train_email_number = session.query(Email).\
        filter(Email.Recipient == recipient).\
        filter(Email.Sender == sender).\
        filter(Email.ToTrain).\
        count()
    session.commit()
    return to_train_email_number


# Get a given number of emails for training with sender different from given sender.
def get_negative_email_for_training(sender, emails_number):
    Session = sessionmaker(bind=engine)
    session = Session()
    emails = session.query(Email).filter(Email.Sender != sender).filter(Email.ToTrain).all()
    random.shuffle(emails)
    return emails[:emails_number]


# Get senders for a given recipient
def get_senders_for_recipient(recipient):
    Session = sessionmaker(bind=engine)
    session = Session()
    result = session.query(Email.Sender).filter(Email.Recipient == recipient).distinct().all()
    return [Sender for Sender, in result]
