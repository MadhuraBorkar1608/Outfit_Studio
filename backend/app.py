from flask import Flask, jsonify
from config import Config
from routes.auth_routes import auth_bp
from routes.profile_routes import profile_bp
from routes.appearance_routes import appearance_bp

app = Flask(__name__)
app.config.from_object(Config)

app.register_blueprint(auth_bp)
app.register_blueprint(profile_bp)
app.register_blueprint(appearance_bp)


@app.route("/api/test", methods=["GET"])
def test_api():
    return jsonify({
        "success": True,
        "message": "Backend is running"
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)