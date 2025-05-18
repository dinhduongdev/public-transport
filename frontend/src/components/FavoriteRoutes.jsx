import { useSelector, useDispatch } from 'react-redux';
import { addFavorite, removeFavorite } from '../features/user/userSlice';

const FavoriteRoutes = () => {
  const user = useSelector((state) => state.user);
  const dispatch = useDispatch();

  const handleAddFavorite = (route) => {
    dispatch(addFavorite(route));
  };

  return (
    <div className="container mx-auto p-6">
      <h2 className="text-2xl font-bold mb-4">Favorite Routes</h2>
      {user.favorites?.length > 0 ? (
        user.favorites.map((route, index) => (
          <div key={index} className="p-4 border rounded-lg mb-2 flex justify-between">
            <div>
              <p>Route: {route.name}</p>
              <p>Stops: {route.stops}</p>
            </div>
            <button
              onClick={() => dispatch(removeFavorite(index))}
              className="text-red-600"
            >
              Remove
            </button>
          </div>
        ))
      ) : (
        <p>No favorite routes yet.</p>
      )}
    </div>
  );
};

export default FavoriteRoutes;