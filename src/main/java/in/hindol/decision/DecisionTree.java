package in.hindol.decision;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class DecisionTree<T> {

    public interface Node<T> {

        T getScore(Map<String, ?> featureMap);
    }

    public static class DecisionNode<T, C> implements Node<T> {

        private final String mFeature;
        private final Class<C> mClazz;
        private final SortedSet<? extends Comparable<C>> mCutPoints;
        private final List<? extends Node<T>> mChildren;

        private DecisionNode(String feature, Class<C> clazz, SortedSet<? extends Comparable<C>> cutPoints, List<? extends Node<T>> children) {
            mFeature = feature;
            mClazz = clazz;
            mCutPoints = cutPoints;
            mChildren = children;

            // TODO: Assert mCutPoints has at least two points and mChildren.size() = mCutPoints.size() - 1.
        }

        @Override
        public T getScore(Map<String, ?> featureMap) {

            C featureValue = mClazz.cast(featureMap.get(mFeature));

            int nextChildIndex = -1;
            for (Comparable<C> cutPoint : mCutPoints) {
                if (cutPoint.compareTo(featureValue) <= 0) {
                    ++nextChildIndex;
                }
            }

            if (nextChildIndex < 0 || nextChildIndex >= mChildren.size()) {
                throw new IllegalArgumentException("Feature value is not within [" + mCutPoints.first() + ", " + mCutPoints.last() + ").");
            }

            Node<T> nextChild = mChildren.get(nextChildIndex);
            return nextChild.getScore(featureMap);
        }

        public static class Builder<T, C> {
            /* TODO */
        }
    }

    public static class EndNode<T> implements Node<T> {

        private final T mScore;

        private EndNode(T score) {
            mScore = score;
        }

        public static <T> EndNode<T> create(T score) {
            return new EndNode<>(score);
        }

        @Override
        public T getScore(Map<String, ?> featureMap) {
            return mScore;
        }
    }
}
