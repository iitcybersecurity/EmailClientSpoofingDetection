from flask_restplus import fields
from Restplus import api


class LoginDto:
    login = api.model('login', {
        'email_address': fields.String(required=True, description='user email address'),
        'password': fields.String(required=True, description='user password'),
        'email_port': fields.String(required=True, description='email server port'),
        'email_imap': fields.String(required=True, description='email server imap'),
        'date_start': fields.DateTime(description='date from which to start downloading e-mails')
    })