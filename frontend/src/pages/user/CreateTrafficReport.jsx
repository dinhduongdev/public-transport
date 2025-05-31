import React, { useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { submitTrafficReport, reverseGeocode } from '../../features/trafficreport/trafficReportsSlice';
import { toast, ToastContainer } from "react-toastify";



const CreateTrafficReport = () => {
  const [formData, setFormData] = useState({
    location: '',
    latitude: '',
    longitude: '',
    description: '',
    status: 'CLEAR',
  });
  const [image, setImage] = useState(null);
  const mapContainer = useRef(null);
  const map = useRef(null);
  const [isGoongLoaded, setIsGoongLoaded] = useState(false);

  // Redux state và dispatch
  const dispatch = useDispatch();
  const { submitLoading, submitError, geocodeLoading, geocodeError } = useSelector(
    (state) => state.trafficReports
  );
  const user = useSelector((state) => state.user);
  console.log("user", user);
  

  // Tải Goong Maps
  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/@goongmaps/goong-js@1.0.9/dist/goong-js.js';
    script.async = true;
    document.head.appendChild(script);

    const link = document.createElement('link');
    link.href = 'https://cdn.jsdelivr.net/npm/@goongmaps/goong-js@1.0.9/dist/goong-js.css';
    link.rel = 'stylesheet';
    document.head.appendChild(link);

    script.onload = () => {
      setIsGoongLoaded(true);
    };

    return () => {
      document.head.removeChild(script);
      document.head.removeChild(link);
    };
  }, []);

  // Khởi tạo bản đồ
  useEffect(() => {
    if (isGoongLoaded && mapContainer.current && !map.current) {
      window.goongjs.accessToken = '2AqrQhr7BHeCfY9LgLINFfIXnjJeBfiwzBtuOoBv';
      map.current = new window.goongjs.Map({
        container: mapContainer.current,
        style: 'https://tiles.goong.io/assets/goong_map_web.json',
        center: [105.83991, 21.02800],
        zoom: 9,
      });

      map.current.on('click', (e) => {
        const lngLat = e.lngLat;
        setFormData({
          ...formData,
          latitude: lngLat.lat.toString(),
          longitude: lngLat.lng.toString(),
        });
        fetchAddress(lngLat.lat, lngLat.lng);
      });
    }
  }, [isGoongLoaded]);

  // Lấy vị trí hiện tại
  const handleGetLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          setFormData({
            ...formData,
            latitude: lat.toString(),
            longitude: lng.toString(),
          });
          map.current.setCenter([lng, lat]);
          new window.goongjs.Marker().setLngLat([lng, lat]).addTo(map.current);
          fetchAddress(lat, lng);
        },
        (error) => {
          console.error('Lỗi lấy vị trí:', error);
        }
      );
    } else {
      console.error('Trình duyệt không hỗ trợ định vị.');
    }
  };

  // Gọi reverse geocode bằng Redux thunk
  const fetchAddress = async (lat, lng) => {
    try {
      const result = await dispatch(reverseGeocode({ latitude: lat, longitude: lng })).unwrap();
      if (result.status === 'OK' && result.results.length > 0) {
        setFormData((prev) => ({
          ...prev,
          location: result.results[0].formatted_address,
        }));
      }
    } catch (error) {
      console.error('Lỗi reverse geocoding:', error);
    }
  };

  // Xử lý thay đổi input
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Xử lý chọn file ảnh
  const handleFileChange = (e) => {
    setImage(e.target.files[0]);
  };

  // Xử lý submit form bằng Redux thunk
  const handleSubmit = async (e) => {
    e.preventDefault();

    const data = new FormData();
    data.append('location', formData.location);
    data.append('latitude', formData.latitude);
    data.append('longitude', formData.longitude);
    data.append('description', formData.description);
    data.append('status', formData.status);
    if (image) {
      data.append('imageUrl', image);
    }
    data.append('userId', user.id); // Thay bằng userId từ auth


    console.log("location", formData.location);
    console.log("latitude", formData.latitude);
    console.log("longitude", formData.longitude);
    console.log("description", formData.description);
    console.log("status", formData.status);
    

    try {
      await dispatch(submitTrafficReport(data)).unwrap();
      setFormData({
        location: '',
        latitude: '',
        longitude: '',
        description: '',
        status: 'CLEAR',
      });
      setImage(null);
      toast.success("Gửi báo cáo thành công");
    } catch (error) {
      console.error('Lỗi khi gửi báo cáo:', error);
      toast.success("Gửi báo cáo thất bại");
    }
  };

  return (
    <div className="container mx-auto mt-8 px-4">
      <button
        onClick={() => window.history.back()}
        className="mb-4 inline-flex items-center px-3 py-2 text-sm font-medium text-white bg-gray-600 rounded-lg hover:bg-gray-700"
      >
        Quay lại
      </button>

      <h1 className="text-2xl font-bold mb-6">Tạo báo cáo giao thông</h1>

      {/* Thông báo từ Redux */}
      {submitLoading && (
        <div className="mb-4 p-4 text-blue-700 bg-blue-100 rounded-lg">
          Đang gửi báo cáo...
        </div>
      )}
      {submitError && (
        <div className="mb-4 p-4 text-red-700 bg-red-100 rounded-lg">
          {submitError || 'Không thể gửi báo cáo. Vui lòng thử lại.'}
        </div>
      )}
      {geocodeError && (
        <div className="mb-4 p-4 text-red-700 bg-red-100 rounded-lg">
          {geocodeError || 'Không thể lấy địa chỉ. Vui lòng nhập thủ công.'}
        </div>
      )}

      {/* Bản đồ Goong Maps */}
      <div
        ref={mapContainer}
        className="mb-6 w-full h-96 rounded-lg shadow-md"
      ></div>

      {/* Form tạo báo cáo */}
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="location" className="block text-sm font-medium text-gray-700">
            Vị trí
          </label>
          <input
            type="text"
            id="location"
            name="location"
            value={formData.location}
            onChange={handleInputChange}
            required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="latitude" className="block text-sm font-medium text-gray-700">
              Vĩ độ
            </label>
            <input
              type="number"
              step="any"
              id="latitude"
              name="latitude"
              value={formData.latitude}
              onChange={handleInputChange}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label htmlFor="longitude" className="block text-sm font-medium text-gray-700">
              Kinh độ
            </label>
            <input
              type="number"
              step="any"
              id="longitude"
              name="longitude"
              value={formData.longitude}
              onChange={handleInputChange}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>

        <div>
          <button
            type="button"
            onClick={handleGetLocation}
            className="mb-4 px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700"
          >
            Lấy vị trí hiện tại
          </button>
        </div>

        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            Mô tả
          </label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            required
            rows="4"
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          ></textarea>
        </div>

        <div>
          <label htmlFor="status" className="block text-sm font-medium text-gray-700">
            Trạng thái
          </label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={handleInputChange}
            required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="CLEAR">Thông thoáng</option>
            <option value="MODERATE">Vừa phải</option>
            <option value="HEAVY">Quá tải</option>
          </select>
        </div>

        <div>
          <label htmlFor="imageUrl" className="block text-sm font-medium text-gray-700">
            Hình ảnh (tùy chọn)
          </label>
          <input
            type="file"
            id="imageUrl"
            accept="image/*"
            onChange={handleFileChange}
            className="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
          />
        </div>

        <button
          type="submit"
          disabled={submitLoading}
          className={`px-4 py-2 text-white rounded-lg ${
            submitLoading ? 'bg-blue-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'
          }`}
        >
          Gửi báo cáo
        </button>
      </form>
    </div>
  );
};

export default CreateTrafficReport;