from flask import Flask, Blueprint
from Restplus import api
from Endpoints.apiEndpoint import ns_endpoint
from Endpoints.humanCheckEndpoint import ns_human_check_endpoint
import settings

app = Flask(__name__)


def configure_app(flask_app):
    flask_app.config['SERVER_NAME'] = settings.FLASK_SERVER_NAME
    flask_app.config['SWAGGER_UI_DOC_EXPANSION'] = settings.RESTPLUS_SWAGGER_UI_DOC_EXPANSION
    flask_app.config['RESTPLUS_VALIDATE'] = settings.RESTPLUS_VALIDATE
    flask_app.config['RESTPLUS_MASK_SWAGGER'] = settings.RESTPLUS_MASK_SWAGGER
    flask_app.config['ERROR_404_HELP'] = settings.RESTPLUS_ERROR_404_HELP
    flask_app.config['SQLALCHEMY_DATABASE_URI'] = settings.SQLALCHEMY_DATABASE_URI
    flask_app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = settings.SQLALCHEMY_TRACK_MODIFICATIONS
    flask_app.config['SQLALCHEMY_ECHO'] = settings.SQLALCHEMY_ECHO


def initialize_app(flask_app):
    configure_app(flask_app)
    blueprint = Blueprint('api', __name__, url_prefix='/flaskAPI')
    api.init_app(blueprint)
    api.add_namespace(ns_endpoint)
    api.add_namespace(ns_human_check_endpoint)
    flask_app.register_blueprint(blueprint)


if __name__ == '__main__':
    initialize_app(app)
    app.run(debug=settings.FLASK_DEBUG)
