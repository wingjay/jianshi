from flask_mail import Message
from flask_mail import Mail
from smtplib import SMTPRecipientsRefused, SMTPHeloError

from server import app
from server.data import errors
logger = app.logger


def send_email(recipients, subject, body=None, html=None):
    mail = Mail(app)
    msg = Message(subject, sender=app.config['EMAIL_ADDRESS'], recipients=recipients)
    msg.body = body
    msg.html = html
    logger.info("start send email to %s", recipients)
    try:
        with app.app_context():
            mail.send(msg)
    except SMTPRecipientsRefused:
        raise errors.EmailReceiverError()
    except SMTPHeloError:
        raise errors.EmailSendError()
