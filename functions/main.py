# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`

from firebase_functions import https_fn, identity_fn
from firebase_admin import initialize_app

from firebase_admin import credentials
from firebase_admin import storage

import requests

# initialize_app()
#
#
# @https_fn.on_request()
# def on_request_example(req: https_fn.Request) -> https_fn.Response:
#     return https_fn.Response("Hello world!")

# @identity_fn.before_user_created()
# def set_profile_avatar(event: identity_fn.AuthBlockingEvent) -> identity_fn.BeforeCreateResponse | None:
#     user = event.data.display_name
#     response = requests.get(f"https://api.dicebear.com/7.x/initials/png?seed={user}")
#     storage_bucket = storage.bucket("grenes-147.appspot.com").blob(f"avatars/{user}.png").upload_from_string(response.content, content_type="image/png")
#     return
