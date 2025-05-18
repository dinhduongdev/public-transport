import { useState } from 'react';
import { db, storage } from '../configs/firebase';
import { collection, addDoc } from 'firebase/firestore';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';

const TrafficReport = () => {
  const [location, setLocation] = useState('');
  const [description, setDescription] = useState('');
  const [image, setImage] = useState(null);
  const [msg, setMsg] = useState('');

  const handleSubmit = async () => {
    try {
      let imageUrl = '';
      if (image) {
        const storageRef = ref(storage, `traffic-images/${image.name}`);
        await uploadBytes(storageRef, image);
        imageUrl = await getDownloadURL(storageRef);
      }

      await addDoc(collection(db, 'traffic-reports'), {
        location,
        description,
        imageUrl,
        timestamp: new Date(),
      });

      setMsg('Traffic report submitted successfully!');
      setLocation('');
      setDescription('');
      setImage(null);
      
    } catch (error) {
      setMsg('Failed to submit report. Please try again.');
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h2 className="text-2xl font-bold mb-4">Report Traffic Issue</h2>
      {msg && <div className="bg-green-100 text-green-700 p-2 rounded mb-4">{msg}</div>}
      <div className="space-y-4">
        <input
          type="text"
          value={location}
          onChange={(e) => setLocation(e.target.value)}
          placeholder="Location"
          className="w-full p-2 border rounded-lg"
        />
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Describe the issue"
          className="w-full p-2 border rounded-lg"
        />
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setImage(e.target.files[0])}
          className="w-full p-2 border rounded-lg"
        />
        <button
          onClick={handleSubmit}
          className="w-full bg-blue-600 text-white p-2 rounded-lg hover:bg-blue-700"
        >
          Submit Report
        </button>
      </div>
    </div>
  );
};

export default TrafficReport;