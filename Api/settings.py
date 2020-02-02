# Flask settings
# FLASK_SERVER_NAME = '192.168.1.238:9999'
FLASK_SERVER_NAME = 'localhost:9999'
FLASK_DEBUG = True  # Do not use debug mode in production

# Flask-Restplus settings
RESTPLUS_SWAGGER_UI_DOC_EXPANSION = 'list'
RESTPLUS_VALIDATE = True
RESTPLUS_MASK_SWAGGER = False
RESTPLUS_ERROR_404_HELP = False

# Database settings
SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://root:$ProjectWork2020@localhost:3306/email-spoofing-detection'
SQLALCHEMY_TRACK_MODIFICATIONS = False
SQLALCHEMY_ECHO = True
