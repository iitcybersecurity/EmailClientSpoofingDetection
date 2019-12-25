from flask import request
from flask_restplus import Resource
from Restplus import api
from DTO.LoginDto import LoginDto
from Service.DBService import get_user
from Service.FileSystemService import create_user_folders
from Service.MailServerConnectionService import download_emails

ns_endpoint = api.namespace('api/login', description='login related operations')
_login = LoginDto.login


@ns_endpoint.route("/", methods=['POST'])
class apiEndpoint(Resource):

    # Rest Api used to make login, if necessary create user and download emails
    @api.doc('Rest Api used to make login, if necessary create user and download emails')
    @api.expect(_login, validate=True)
    def post(self):
        login_data = request.get_json()
        email_address = login_data['email_address']
        existing_user = get_user(email_address)
        response_object = ""
        if(not existing_user):
            create_user_folders(email_address)

        download_emails(login_data)
        response_object = {
            'status': 'success',
            'message': 'Success',
        }

        return response_object, 200
