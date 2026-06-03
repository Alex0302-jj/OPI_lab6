package org.example;

import java.time.LocalDate;
import java.util.List;

public class Algorithms {

    // --- 1. БІНАРНИЙ ПОШУК (Пошук за датою) ---
    public static Training binarySearchRecursive(List<Training> sortedList, LocalDate key, int low, int high) {
        if (low > high) {
            return null; // Не знайдено
        }

        int mid = low + ((high - low) / 2);
        Training midVal = sortedList.get(mid);

        // Порівняння дат
        if (midVal.getDate().equals(key)) {
            return midVal;
        } else if (midVal.getDate().compareTo(key) < 0) {
            // Шукана дата більша -> йдемо в праву половину
            return binarySearchRecursive(sortedList, key, mid + 1, high);
        } else {
            // Шукана дата менша -> йдемо в ліву половину
            return binarySearchRecursive(sortedList, key, low, mid - 1);
        }
    }

    // --- 2. ДИНАМІЧНЕ ПРОГРАМУВАННЯ (Рюкзак) ---
    // ОНОВЛЕНИЙ МЕТОД: Тепер показує список обраних вправ
    public static String solveKnapsack(List<Training> items, int capacityMinutes) {
        int n = items.size();
        int[][] dp = new int[n + 1][capacityMinutes + 1];

        // 1. Заповнюємо таблицю (шукаємо максимум користі)
        for (int i = 1; i <= n; i++) {
            Training t = items.get(i - 1);
            int weight = t.getDurationMin();
            int value = t.getEfficiency();

            for (int w = 0; w <= capacityMinutes; w++) {
                if (weight <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - weight] + value);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // 2. "Відновлюємо" відповідь: дізнаємося, які саме тренування ми взяли
        StringBuilder selectedNames = new StringBuilder();
        int w = capacityMinutes;
        for (int i = n; i > 0 && w > 0; i--) {
            // Якщо значення в цій клітинці відрізняється від верхньої,
            // значить ми поклали цей предмет у "рюкзак"
            if (dp[i][w] != dp[i - 1][w]) {
                Training t = items.get(i - 1);
                selectedNames.append(t.getName()).append(", ");
                w -= t.getDurationMin(); // віднімаємо час (вагу)
            }
        }

        if (selectedNames.length() > 0) {
            // Прибираємо зайву кому в кінці
            selectedNames.setLength(selectedNames.length() - 2);

            return "Оптимальний план на " + capacityMinutes + " хв:\n" +
                    "Вправи: " + selectedNames.toString() + "\n" +
                    "Сумарна ефективність: " + dp[n][capacityMinutes];
        } else {
            return "На жаль, на цей час неможливо підібрати тренування.";
        }
    }

    // --- 3. АЛГОРИТМ ПРІМА (Мережа залів) ---
    public static String runPrimsAlgorithm() {
        int V = 5;
        // Матриця відстаней (0 означає відсутність прямого шляху)
        int[][] graph = {
                { 0, 2, 0, 6, 0 },
                { 2, 0, 3, 8, 5 },
                { 0, 3, 0, 0, 7 },
                { 6, 8, 0, 0, 9 },
                { 0, 5, 7, 9, 0 }
        };

        int[] key = new int[V];
        int[] parent = new int[V];
        boolean[] mstSet = new boolean[V];

        for (int i = 0; i < V; i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < V - 1; count++) {
            int u = minKey(key, mstSet, V);
            mstSet[u] = true;

            for (int v = 0; v < V; v++) {
                if (graph[u][v] != 0 && !mstSet[v] && graph[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
            }
        }
        return printMST(parent, graph, V);
    }

    private static int minKey(int[] key, boolean[] mstSet, int V) {
        int min = Integer.MAX_VALUE, min_index = -1;
        for (int v = 0; v < V; v++) {
            if (!mstSet[v] && key[v] < min) {
                min = key[v];
                min_index = v;
            }
        }
        return min_index;
    }

    private static String printMST(int[] parent, int[][] graph, int V) {
        StringBuilder sb = new StringBuilder("Оптимальна мережа залів (Прім):\n");
        int totalWeight = 0;
        String[] gymNames = {"Центр", "Північ", "Схід", "Південь", "Захід"};

        for (int i = 1; i < V; i++) {
            sb.append(gymNames[parent[i]]).append(" - ").append(gymNames[i])
                    .append("\t : ").append(graph[i][parent[i]]).append(" км\n");
            totalWeight += graph[i][parent[i]];
        }
        sb.append("Загальна довжина кабелю: ").append(totalWeight).append(" км");
        return sb.toString();
    }
}