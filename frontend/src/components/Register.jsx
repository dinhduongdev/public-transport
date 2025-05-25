import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Apis, endpoints } from "../configs/apis";

const Register = () => {
  const [user, setUser] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirm: "",
  });
  const [verificationCode, setVerificationCode] = useState("");
  const avatar = useRef();
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [codeSent, setCodeSent] = useState(false);
  const navigate = useNavigate();

  const setState = (value, field) => {
    setUser({ ...user, [field]: value });
  };

  const sendVerificationCode = async () => {
    if (!user.email) {
      setMsg("Please enter your email before requesting a verification code.");
      return;
    }
    try {
      await Apis.post(endpoints["send-verification-code"], null, {
        params: { email: user.email },
      });
      setCodeSent(true);
      setMsg("Verification code has been sent to your email.");
    } catch (error) {
      console.error(error);
      setMsg("Failed to send verification code. Please try again later.");
    }
  };

  const register = async (e) => {
    e.preventDefault();
    setMsg("");

    if (user.password !== user.confirm) {
      setMsg("Passwords do not match.");
      return;
    }
    if (!verificationCode) {
      setMsg("Please enter the verification code.");
      return;
    }

    const form = new FormData();
    for (const key in user) {
      if (key !== "confirm") form.append(key, user[key]);
    }
    form.append("verificationCode", verificationCode);
    if (avatar.current.files[0]) {
      form.append("avatar", avatar.current.files[0]);
    }

    try {
      setLoading(true);
      await Apis.post(endpoints["register"], form, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      navigate("/login");
    } catch (ex) {
      console.error(ex);
      setMsg(
        "Registration failed. Please check your information and try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-sm">
        <h1 className="text-2xl font-semibold text-center mb-4">Sign Up</h1>

        {msg && (
          <div className="bg-red-100 text-red-700 p-2 rounded mb-4 text-center text-sm">
            {msg}
          </div>
        )}

        <form onSubmit={register} className="space-y-4">
          <input
            type="text"
            value={user.firstName}
            onChange={(e) => setState(e.target.value, "firstName")}
            placeholder="First Name"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <input
            type="text"
            value={user.lastName}
            onChange={(e) => setState(e.target.value, "lastName")}
            placeholder="Last Name"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <div className="flex space-x-2">
            <input
              type="email"
              value={user.email}
              onChange={(e) => setState(e.target.value, "email")}
              placeholder="Email"
              required
              className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
            />
            <button
              type="button"
              onClick={sendVerificationCode}
              className="bg-blue-600 text-white px-2 rounded hover:bg-blue-700 transition"
            >
              Send Code
            </button>
          </div>
          <input
            type="text"
            value={verificationCode}
            onChange={(e) => setVerificationCode(e.target.value)}
            placeholder="Verification Code"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <input
            type="password"
            value={user.password}
            onChange={(e) => setState(e.target.value, "password")}
            placeholder="Password"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <input
            type="password"
            value={user.confirm}
            onChange={(e) => setState(e.target.value, "confirm")}
            placeholder="Confirm Password"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400"
          />
          <input
            ref={avatar}
            type="file"
            required
            className="w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          {loading ? (
            <div className="flex justify-center">
              <svg
                className="animate-spin h-5 w-5 text-blue-500"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                ></circle>
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
            </div>
          ) : (
            <button
              type="submit"
              className="w-full bg-blue-600 text-white p-2 rounded-lg hover:bg-blue-700 transition"
            >
              Sign Up
            </button>
          )}
        </form>

        <p className="mt-4 text-center text-sm text-gray-600">
          Already have an account?{" "}
          <a href="/login" className="text-blue-500 hover:underline">
            Sign in
          </a>
        </p>
      </div>
    </div>
  );
};

export default Register;
