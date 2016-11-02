from validate_email import validate_email


def check_email_format(email):
    if not email:
        return False
    return validate_email(email)
