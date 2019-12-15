from app import db


class User(db.Model):
    recipient: db.Column(db.String(100), primary_key=True, nullable=False)
    sender: db.Column(db.String(100), primary_key=True, nullable=False)
    timestamp: db.Column(db.DateTime, primary_key=True, nullable=False)
    toTrain: db.Column(db.Boolean, nullable=False)
    toTest: db.Column(db.Boolean, nullable=False)
    trained: db.Column(db.Boolean, nullable=False)
    tested: db.Column(db.Boolean, nullable=False)
    prediction: db.Column(db.Integer, nullable=True)
    trained: db.Column(db.Boolean, nullable=False)

    def __repr__(self):
        return '<From {} - To {} - {}->'.format(self.recipient, self.sender, self.timestamp)
