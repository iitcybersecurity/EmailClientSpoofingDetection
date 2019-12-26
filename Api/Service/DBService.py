from sqlalchemy.orm import sessionmaker
from sqlalchemy import desc
from database import engine
from DBModels.Email import Email


# Get user from db if exists
def get_user(recipient):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == recipient).first()


# Get timestamp of last email on db for a specific recipient
def get_last_timestamp_by_recipient(recipient):
    Session = sessionmaker(bind=engine)
    session = Session()
    last_timestamp = session.query(Email).filter(Email.Recipient == recipient).order_by(desc(Email.Timestamp)).first().Timestamp
    if(last_timestamp):
        last_timestamp = last_timestamp.date()
    return last_timestamp


# Verify if an email already exists on db
def email_exists(recipient, sender, timestamp):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == recipient).filter(Email.Sender == sender).filter(Email.Timestamp == timestamp).scalar() is not None


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
