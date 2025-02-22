package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Review;
import com.java_assignment.group.Model.Transaction;
import com.java_assignment.group.Model.TxtModelRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewController {
    private TxtModelRepository<Review> reviewRepository;

    public ReviewController() throws IOException {
        // review.txtをデータファイルとして使用し、ReviewのCSV変換関数を指定
        reviewRepository = new TxtModelRepository<>("src/Data/review.txt", Review::fromCsv, Review::toCsv);
    }

    /**
     * すべてのレビューを取得します。
     */
    public List<Review> getAllReviews() {
        try {
            return reviewRepository.readAll();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load reviews", e);
        }
    }

    /**
     * すべてのレビューを取得します。
     */
    public List<Review> getAllReviewsFromUser(String baseUserId) {
        try {
            List<Review> result = new ArrayList<>();
            List<Review> reviews = reviewRepository.readAll();
            for (Review item : reviews) {
                if (item.getUserId().equals(baseUserId)) {
                    result.add(item);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load reviews", e);
        }
    }

    /**
     * すべてのレビューを取得します。
     */
    public List<Review> getAllReviewsToUser(String baseUserId) {
        try {
            List<Review> result = new ArrayList<>();
            List<Review> reviews = reviewRepository.readAll();
            for (Review item : reviews) {
                if (item.getTargetUserId().equals(baseUserId)) {
                    result.add(item);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load reviews", e);
        }
    }


    /**
     * 指定されたreviewIdのレビューを取得します。
     */
    public Review getReviewById(String reviewId) {
        try {
            List<Review> reviews = reviewRepository.readAll();
            for (Review review : reviews) {
                if (review.getId().equals(reviewId)) {
                    return review;
                }
            }
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 新規レビューを作成します。（POST相当）
     */
    public boolean createReview(Review newReview) {
        try {
            List<Review> reviews = reviewRepository.readAll();
            reviews.add(newReview);
            reviewRepository.writeAll(reviews, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 既存のレビューを更新します。（PUT相当）
     */
    public boolean updateReview(Review updatedReview) {
        try {
            List<Review> reviews = reviewRepository.readAll();
            for (int i = 0; i < reviews.size(); i++) {
                if (reviews.get(i).getId().equals(updatedReview.getId())) {
                    reviews.set(i, updatedReview);
                    break;
                }
            }
            reviewRepository.writeAll(reviews, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * reviewIdに基づいてレビューを削除します。
     */
    public boolean deleteReview(String reviewId) {
        try {
            List<Review> reviews = reviewRepository.readAll();
            List<Review> updatedReviews = reviews.stream()
                    .filter(review -> !review.getId().equals(reviewId))
                    .collect(Collectors.toList());
            reviewRepository.writeAll(updatedReviews, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 指定されたbaseUserIdがtargetUserIdとなっているすべてのレビューのratingの平均を計算します。
     *
     * @param baseUserId 対象のユーザーID
     * @return 平均評価（該当レビューがない場合は0.0）
     */
    public double getAverageRatingByBaseUserId(String baseUserId) {
        try {
            List<Review> reviews = reviewRepository.readAll();
            // targetUserIdが指定されたbaseUserIdと一致するレビューを抽出
            List<Review> targetReviews = reviews.stream()
                    .filter(review -> review.getTargetUserId().equals(baseUserId))
                    .collect(Collectors.toList());
            if (targetReviews.isEmpty()) {
                return 0.0;
            }
            double sum = targetReviews.stream().mapToInt(Review::getRating).sum();
            return sum / targetReviews.size();
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
}
