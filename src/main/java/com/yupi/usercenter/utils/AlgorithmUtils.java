package com.yupi.usercenter.utils;
import java.util.List;

public class AlgorithmUtils {
        public static int minDistance(List<String> list1, List<String> list2) {
            int m = list1.size();
            int n = list2.size();

            // dp[i][j] 表示 word1[0..i-1] 到 word2[0..j-1] 的最小编辑距离
            int[][] dp = new int[m + 1][n + 1];

            // 初始化：将 word1 的前 i 个字符变为空串，需要 i 次删除
            for (int i = 0; i <= m; i++) dp[i][0] = i;
            // 将空串变为 word2 的前 j 个字符，需要 j 次插入
            for (int j = 0; j <= n; j++) dp[0][j] = j;

            // 填表
            for (int i = 1; i <= m; i++) {
                String s1 = list1.get(i - 1);
                for (int j = 1; j <= n; j++) {
                    String s2 = list2.get(j - 1);
                    if (s1.equals(s2)) {
                        dp[i][j] = dp[i - 1][j - 1]; // 不需要操作
                    } else {
                        dp[i][j] = Math.min(
                                dp[i - 1][j - 1],  // 替换
                                Math.min(
                                        dp[i][j - 1],  // 插入
                                        dp[i - 1][j]   // 删除
                                )
                        ) + 1;
                    }
                }
            }

            return dp[m][n];
        }
}
