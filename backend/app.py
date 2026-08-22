from flask import Flask, jsonify
from config import Config

app = Flask(__name__)
app.config.from_object(Config)


@app.route("/api/test", methods=["GET"])
def test_api():
    return jsonify({
        "success": True,
        "message": "Backend is running"
    })


if __name__ == "__main__":
    app.run(debug=True)