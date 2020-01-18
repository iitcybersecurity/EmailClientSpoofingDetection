from flask_restplus import fields
from Restplus import api


class EmailCheckedDto:
    emailChecked = api.model('emailChecked', {
        'recipient': fields.String(required=True, description='email recipient'),
        'sender': fields.String(required=True, description='email sender'),
        'timestamp': fields.DateTime(required=True, description='email timestamp')

    })


# class HumanCheckDto:
#     humanCheck = api.model('humanCheck', {
#         'emails_checked': fields.List(fields.Nested, required=True, description='user email address'),
#     })

