from database import engine, Base
from sqlalchemy import Column, String, DateTime, Boolean, Integer


class Email(Base):
    __tablename__ = 'emails'
    __table_args__ = {'autoload': True, 'autoload_with': engine}
    Recipient: Column(String(100), primary_key=True, nullable=False)
    Sender: Column(String(100), primary_key=True, nullable=False)
    Timestamp: Column(DateTime, primary_key=True, nullable=False)
    ToTrain: Column(Boolean, nullable=False)
    ToTest: Column(Boolean, nullable=False)
    Trained: Column(Boolean, nullable=False)
    Tested: Column(Boolean, nullable=False)
    Prediction: Column(Integer, nullable=True)
    Verified: Column(Boolean, nullable=False)

    def __init__(self, recipient, sender, timestamp, toTrain=False, toTest=False, trained=False, tested=False, prediction=None, verified=False):
        self.Recipient = recipient
        self.Sender = sender
        self.Timestamp = timestamp
        self.ToTrain = toTrain
        self.ToTest = toTest
        self.Trained = trained
        self.Tested = tested
        self.Prediction = prediction
        self.Verified = verified
  
