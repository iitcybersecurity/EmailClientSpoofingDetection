from sqlalchemy.orm import sessionmaker
from database import engine
from DBModels.Email import Email


def get_user(email_address):
    Session = sessionmaker(bind=engine)
    session = Session()
    return session.query(Email).filter(Email.Recipient == email_address).first()
