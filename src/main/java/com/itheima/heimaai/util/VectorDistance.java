package com.itheima.heimaai.util;

public class VectorDistance {
    /**
     * 计算余弦相似度
     * 返回值范围 [-1, 1]，越接近 1 表示越相似
     */
    public static double cosineSimilarity(float[] v1, float[] v2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            normA += Math.pow(v1[i], 2);
            normB += Math.pow(v2[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 计算欧式距离
     * 返回值越小表示越相似（距离越近）
     */
    public static double euclideanDistance(float[] v1, float[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.pow(v1[i] - v2[i], 2);
        }
        return Math.sqrt(sum);
    }
}
