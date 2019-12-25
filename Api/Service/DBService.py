from sqlalchemy.orm import sessionmaker
from database import engine
from DBModels.Email import Email


def get_user(email_address):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == email_address).first()


def insert_to_train_email(recipient, sender, timestamp):
    email = Email(recipient, sender, timestamp, toTrain=True)
    Session = sessionmaker(bind=engine)
    session = Session()
    session.add(email)
    session.commit()
