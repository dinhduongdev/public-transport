import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

const UserProfile = () => {
  const user = useSelector((state) => state.user);
  const navigate = useNavigate();

  if (!user) {
    navigate('/login'); 
    return null;
  }
  
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
        <h1 className="text-3xl font-bold text-center text-green-600 mb-6">
          Hồ sơ người dùng
        </h1>
        <div className="space-y-4">
          <p>
            <strong>Tên:</strong> {user.lastname}
          </p>
          <p>
            <strong>Họ:</strong> {user.firstname}
          </p>
          <p>
            <strong>Email:</strong> {user.email}
          </p>
          {user.avatar && (
            <img
              src={user.avatar}
              alt="Avatar"
              className="w-32 h-32 rounded-full mx-auto"
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default UserProfile;