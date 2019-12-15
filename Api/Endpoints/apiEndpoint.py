from flask import Response
from flask_restplus import Resource
from Restplus import api
from DTO.LoginDto import LoginDto


# from Endpoints.Utils import JsonTransformer

ns_endpoint = LoginDto.endpoint
# api = LoginDto.api
_login = LoginDto.login

@ns_endpoint.route("/", methods=['POST'])
class apiEndpoint(Resource):
    # def get(self):
    #     return 'Hello, world!'

    #Rest Api used to make login, if necessary create user and download emails
    @api.doc('Rest Api used to make login, if necessary create user and download emails')
    @api.expect(_login, validate=True)
    def post(self):
        response_object = {
            'status': 'success',
            'message': 'Successfully downloaded emails'
        }

        return response_object, 200

















