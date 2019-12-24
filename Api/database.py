# from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
import settings

# db = SQLAlchemy()
engine = create_engine(settings.SQLALCHEMY_DATABASE_URI, echo=True)
Base = declarative_base(engine)
