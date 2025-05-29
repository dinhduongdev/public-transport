import React, { useState, useEffect } from "react";
import { Star, User } from "lucide-react";
import { format } from "date-fns";
import { useSelector, useDispatch } from "react-redux";
import { submitRating } from "../features/ratings/ratingSlice";
import { toast } from "react-toastify";

const RouteReview = ({ summary, reviews, routeId, onReviewSubmitted }) => {
  const [userRating, setUserRating] = useState(0);
  const [userComment, setUserComment] = useState("");
  const [currentUserReview, setCurrentUserReview] = useState(null);

  const dispatch = useDispatch();
  const user = useSelector((state) => state.user);
  const { error } = useSelector((state) => state.ratings);
  console.log("user", user);

  useEffect(() => {
    if (!user || !user.id) {
      // If not logged in, reset state to default
      setCurrentUserReview(null);
      setUserRating(0);
      setUserComment("");
      return;
    }
    const existingReview = reviews.find((review) =>
      review.user.id === user.id ? user.id : null
    );
    if (existingReview) {
      setCurrentUserReview(existingReview);
      setUserRating(existingReview.score);
      setUserComment(existingReview.comment || "");
    } else {
      setCurrentUserReview(null);
      setUserRating(0);
      setUserComment("");
    }
  }, [reviews, user]);

  const getBarWidth = (count) => {
    const max = Math.max(...Object.values(summary.ratingDistribution));
    return `${max > 0 ? (count / max) * 100 : 0}%`;
  };

  const handleRatingSubmit = async (e) => {
    e.preventDefault();
    if (userRating < 1 || userRating > 5) {
      dispatch(
        submitRating({ error: "Please select a rating from 1 to 5 stars." })
      );
      return;
    }

    dispatch(
      submitRating({
        userId: user.id,
        routeId,
        score: userRating,
        comment: userComment,
      })
    ).then((result) => {
      if (result.meta.requestStatus === "fulfilled") {
        toast.success("You have successfully rated this route!");
        onReviewSubmitted();
      }
    });
  };

  return (
    <div className="p-4">
      {/* User Review Form */}
      <div className="mb-4 py-3 rounded-xl shadow bg-gray-100">
        <h3 className="text-lg font-semibold mb-2 text-center">
          {currentUserReview ? "Update Your Review" : "Write a Review"}
        </h3>
        {error && (
          <div className="flex items-center justify-center gap-2 mb-2">
            <p className="text-sm text-red-600">{error}</p>
            {error.includes("login") && (
              <a
                href="/login"
                className="text-sm text-blue-600 hover:underline"
              >
                Log In
              </a>
            )}
          </div>
        )}
        <form onSubmit={handleRatingSubmit} className="space-y-3 px-4">
          <div className="flex items-center justify-center gap-2">
            <span className="text-sm text-gray-600">Your Rating:</span>
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                onClick={() => setUserRating(star)}
                className={`text-2xl ${
                  star <= userRating ? "text-yellow-400" : "text-gray-300"
                }`}
              >
                ★
              </button>
            ))}
          </div>
          <textarea
            value={userComment}
            onChange={(e) => setUserComment(e.target.value)}
            placeholder="Enter your comment..."
            className="w-full p-2 border rounded text-sm text-gray-600"
            rows="3"
          ></textarea>
          <button
            type="submit"
            className="w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            {currentUserReview ? "Update Review" : "Submit Review"}
          </button>
        </form>
      </div>

      {/* Review Summary */}
      <div className="mb-5">
        <h3 className="text-lg font-semibold mb-2">REVIEW SUMMARY</h3>
        <div className="flex items-center mb-1">
          <span className="text-3xl font-bold mr-2">
            {summary.averageScore.toFixed(1)}
          </span>
          <div className="flex items-center text-yellow-400">
            {Array.from({ length: 5 }).map((_, i) => (
              <Star
                key={i}
                fill={
                  i < Math.floor(summary.averageScore) ? "currentColor" : "none"
                }
                strokeWidth={1.5}
                className="w-5 h-5"
              />
            ))}
          </div>
        </div>
        <p className="text-gray-600 flex items-center mb-3">
          <User className="w-4 h-4 mr-1" /> {summary.totalRatings} reviews
        </p>
        <div className="space-y-1">
          {[5, 4, 3, 2, 1].map((score) => (
            <div key={score} className="flex items-center">
              <span className="w-5 text-sm">{score}</span>
              <div className="flex-1 h-3 mx-2 bg-gray-200 rounded">
                <div
                  className={`h-3 rounded ${
                    score >= 4
                      ? "bg-green-500"
                      : score === 3
                      ? "bg-yellow-300"
                      : "bg-red-500"
                  }`}
                  style={{
                    width: getBarWidth(summary.ratingDistribution[score] || 0),
                  }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Reviews List */}
      <h4 className="text-lg font-semibold mb-2">USER REVIEWS</h4>
      <div className="space-y-4">
        {reviews.map((review) => (
          <div key={review.id} className="flex items-start border-b pb-3">
            <div className="w-10 h-10 rounded-full overflow-hidden bg-gray-200 mr-3">
              {review.user.avatar ? (
                <img
                  src={review.user.avatar}
                  alt={`${review.user.firstname} ${review.user.lastname}`}
                  className="w-full h-full object-cover"
                />
              ) : (
                <User className="w-full h-full p-2 text-gray-500" />
              )}
            </div>
            <div className="flex-1">
              <p className="font-semibold">
                {review.user.firstname} {review.user.lastname}
              </p>
              <p className="text-sm text-gray-500">
                {format(new Date(review.updatedAt), "dd/MM/yyyy HH:mm:ss")}
              </p>
              {review.comment && (
                <p className="text-sm text-gray-600 mt-1">{review.comment}</p>
              )}
            </div>
            <div className="flex items-center text-yellow-400">
              {Array.from({ length: 5 }).map((_, i) => (
                <Star
                  key={i}
                  fill={i < review.score ? "currentColor" : "none"}
                  strokeWidth={1.5}
                  className="w-5 h-5"
                />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RouteReview;
