import React, { useEffect } from "react";
import {
  fetchRatingData,
  clearRatingData,
} from "../../features/ratings/ratingSlice";
import { useDispatch, useSelector } from "react-redux";
import RouteReview from "../../components/RouteReview";

const RouteReviews = ({ routeId }) => {
  const dispatch = useDispatch();
  const { ratingData, loading, error } = useSelector((state) => state.ratings);

  // Fetch dữ liệu đánh giá khi routeId thay đổi
  useEffect(() => {
    if (routeId) {
      dispatch(fetchRatingData(routeId));
    } else {
      dispatch(clearRatingData());
    }
  }, [dispatch, routeId]);

  const handleReviewSubmitted = () => {    
    if (routeId) {
      dispatch(fetchRatingData(routeId)); // refresh
    }
  };
  console.log("ratingData", ratingData);

  return (
    <>
      {loading ? (
        <p className="text-sm text-gray-600">Loading review information...</p>
      ) : error ? (
        <div className="flex items-center gap-2">
          <p className="text-sm text-red-600">{error}</p>
          <a href="/login" className="text-sm text-blue-600 hover:underline">
            Sign Up
          </a>
        </div>
      ) : ratingData ? (
        <RouteReview
          summary={{
            averageScore: ratingData.averageScore,
            totalRatings: ratingData.totalRatings,
            ratingDistribution: ratingData.ratingDistribution,
          }}
          reviews={ratingData.ratings}
          routeId={routeId}
          onReviewSubmitted={handleReviewSubmitted}
        />
      ) : (
        <p className="text-sm text-gray-600">There are no reviews yet.</p>
      )}
    </>
  );
};

export default RouteReviews;
