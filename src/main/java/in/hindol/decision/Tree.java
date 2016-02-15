package in.hindol.decision;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {

    private final Node<T> mRootNode;

    public Tree(Node<T> rootNode) {
        mRootNode = rootNode;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public T evaluate(List<?> parameters) {

        if (mRootNode.isEndNode()) {
            return mRootNode.toEndNode().value();
        }

        Node<T> node = mRootNode;
        for (Object parameter : parameters) {
            node = node.toDecisionNode().next(parameter);

            if (node.isEndNode()) {
                return node.toEndNode().value();
            }
        }

        throw new IllegalArgumentException("The given set of parameters are not enough to reach a decision.");
    }

    public interface Condition {

        <I> int apply(I input);
    }

    public interface Node<T> {

        boolean isDecisionNode();
        boolean isEndNode();

        EndNode<T> toEndNode();
        DecisionNode<T> toDecisionNode();
    }

    private static class DecisionNode<T> implements Node<T> {

        public final List<? extends Node<T>> mChildren = new ArrayList<>();
        public final Condition mCondition;

        public DecisionNode(Condition condition) {
            mCondition = condition;
        }

        @Override
        public boolean isDecisionNode() {
            return true;
        }

        @Override
        public boolean isEndNode() {
            return false;
        }

        @Override
        public DecisionNode<T> toDecisionNode() {
            return this;
        }

        @Override
        public EndNode<T> toEndNode() {
            throw new UnsupportedOperationException("Not an end node.");
        }

        public <I> Node<T> next(I input) {
            return mChildren.get(mCondition.apply(input));
        }
    }

    private static class EndNode<T> implements Node<T> {

        private final T mValue;

        public EndNode(T value) {
            mValue = value;
        }

        @Override
        public boolean isDecisionNode() {
            return false;
        }

        @Override
        public boolean isEndNode() {
            return true;
        }

        @Override
        public DecisionNode<T> toDecisionNode() {
            throw new UnsupportedOperationException("Not a decision node.");
        }

        @Override
        public EndNode<T> toEndNode() {
            return this;
        }

        public T value() {
            return mValue;
        }
    }

    public static class Builder<T> {

        private Node<T> mRootNode;

        public Tree<T> build() {
            return new Tree<>(mRootNode);
        }
    }
}
