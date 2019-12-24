from flask import request
from flask_restplus import Resource
from Restplus import api
from DTO.LoginDto import LoginDto
from Service.DBService import get_user

ns_endpoint = api.namespace('api/login', description='login related operations')
_login = LoginDto.login


@ns_endpoint.route("/", methods=['POST'])
class apiEndpoint(Resource):

    # Rest Api used to make login, if necessary create user and download emails
    @api.doc('Rest Api used to make login, if necessary create user and download emails')
    @api.expect(_login, validate=True)
    def post(self):
        data = request.get_json()
        existing_user = get_user(data['email_address'])
        response_object = ""
        if(not existing_user):
            response_object = {
                'status': 'success',
                'message': 'User does not exist',
            }
        else:
            response_object = {
                'status': 'success',
                'message': 'User exists',
            }

        return response_object, 200
