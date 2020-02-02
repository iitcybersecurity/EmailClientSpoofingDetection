from flask import request
from flask_restplus import Resource
from Restplus import api
from DTO.EmailCheckedDto import EmailCheckedDto
from Service.DBService import report_human_prediction

ns_human_check_endpoint = api.namespace(
    'human-check', description='human check')
_emailChecked = EmailCheckedDto.emailChecked


@ns_human_check_endpoint.route("", methods=['POST'])
class humanCheckEndpoint(Resource):

    # Rest Api used to make manually check emails validity
    @api.doc('Rest Api used to make manually check emails validity')
    @api.expect([_emailChecked], validate=True)
    def post(self):
        # Get data from request
        human_check_data = request.get_json()
        # Report human prediction
        report_human_prediction(human_check_data)
        response_object = {
            'status': 'success',
            'message': 'Success',
        }
        return response_object, 200
