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
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-2xl p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">HỒ SƠ</h2>
        </div>

        <div className="flex items-center gap-4 mb-6">
          <img
            src={user.avatar}
            alt="avatar"
            className="w-16 h-16 rounded-full object-cover"
          />
          <div>
            <p className="font-medium">{`${user.firstname} ${user.lastname}`}</p>
            <p className="text-sm text-gray-500">Tên</p>
          </div>
        </div>


        <div className="space-y-4 mb-8">
          <div>
            <label className="block text-sm font-medium mb-1">Họ và tên</label>
            <input
              type="text"
              value={`${user.firstname} ${user.lastname}`}
              readOnly
              className="w-full border rounded px-3 py-2 bg-gray-100 cursor-not-allowed"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Địa chỉ gmail</label>
            <input
              type="email"
              value={user.email}
              readOnly
              className="w-full border rounded px-3 py-2 bg-gray-100 cursor-not-allowed"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Vai trò</label>
            <select
              disabled
              value={user.role || 'User'}
              className="w-full border rounded px-3 py-2 bg-gray-100 cursor-not-allowed"
            >
              <option>User</option>
              <option>Product manager</option>
              <option>Admin</option>
            </select>
          </div>
        </div>

      
      </div>
    </div>
  );
};

export default UserProfile;