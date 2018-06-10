package com.yhxx.common.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合扩展工具
 *
 * @author zsp
 * @since 2017-07-26
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    public static <T> T[] asArray(List<T> list, T[] array) {
        if (list == null) {
            return array;
        }

        return list.toArray(array);
    }

    public static <T> List<T> copy(List<T> list) {
        if (list == null) {
            return null;
        }

        List<T> newList = new ArrayList<>();
        for (T each : list) {
            newList.add(each);
        }
        return newList;
    }

    public static <T> List<T> reverse(List<T> list) {
        if (list == null) {
            return null;
        }

        List<T> newList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            newList.add(list.get(i));
        }
        return newList;
    }

    public static <T> List<T> sort(List<T> list, Comparator<T> comparable) {
        List<T> newList = copy(list);
        if (!isEmpty(newList)) {
            // 重新排序
            Collections.sort(newList, comparable);
        }
        return newList;
    }

    /**
     * list去重
     *
     * @param list
     * @param comparable
     * @param <T>
     * @return
     */
    public static <T> List<T> distinct(List<T> list, Comparator<T> comparable) {
        List<T> distinctList = new ArrayList<>();

        for (T each : list) {
            int idx = Collections.binarySearch(distinctList, each, comparable);
            if (idx < 0) {
                // 如果不存在才能加入
                distinctList.add(each);
            }
        }

        return distinctList;
    }

    /**
     * 判断集合是否为null或空集合
     *
     * @param col 集合
     * @return true表示为null或空集合，false表示非空。
     */
    public static <T> boolean isNullOrEmpty(Collection<T> col) {
        return col == null || col.size() == 0;
    }

    /**
     * 获取列表的第一个对象。
     *
     * @param list 列表集合
     * @return 返回第一个对象，集合为空则返回null。
     */
    public static <T> T first(List<T> list) {
        if (isNullOrEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 获取列表的最后一个对象。
     *
     * @param list 列表集合
     * @return 返回最后一个对象，集合为空则返回null。
     */
    public static <T> T last(List<T> list) {
        if (isNullOrEmpty(list)) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    /**
     * 获取列表中指定索引位置的对象。
     *
     * @param list 列表集合
     * @return 返回指定索引位置的对象，集合为空则返回null。
     */
    public static <T> T elementAt(List<T> list, int index) {
        if (isNullOrEmpty(list)) {
            return null;
        } else {
            if (index < 0 || index > list.size() - 1) {
                throw new IndexOutOfBoundsException();
            }
            return list.get(index);
        }
    }

    /**
     * 获取列表中符合条件的对象。
     *
     * @param col 集合
     * @return 返回符合条件的对象，否则返回null。
     */
    public static <T> T findOne(Collection<T> col, Predicate<T> predicate) {
        T found = null;
        if (col != null && col.size() > 0) {
            for (T item : col) {
                if (predicate.test(item)) {
                    found = item;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * 获取列表中符合条件的对象集合。
     *
     * @param col 集合
     * @return 返回符合条件的对象集合，否则返回空集合。
     */
    public static <T> List<T> findAll(Collection<T> col, Predicate<T> predicate) {
        List<T> found = new ArrayList<T>();
        if (col != null && col.size() > 0) {
            for (T item : col) {
                if (predicate.test(item)) {
                    found.add(item);
                }
            }
        }
        return found;
    }

    /**
     * 获取集合中指定的字段，并以列表返回
     */
    public static <TType, TField> List<TField> listField(Collection<TType> col,
                                                         Function<TType, TField> fieldFetcher) {
        return listField(col, fieldFetcher, null, false);
    }

    /**
     * 获取集合中指定的字段，并以列表返回
     */
    public static <TType, TField> List<TField> listField(Collection<TType> col,
                                                         Function<TType, TField> fieldFetcher,
                                                         Predicate<TType> filter) {
        return listField(col, fieldFetcher, filter, false);
    }

    /**
     * 获取集合中指定的字段，并以列表返回
     */
    public static <TType, TField> List<TField> listField(Collection<TType> col,
                                                         Function<TType, TField> fieldFetcher,
                                                         boolean distinct) {
        return listField(col, fieldFetcher, null, distinct);
    }

    /**
     * 获取集合中指定的字段，并以列表返回
     */
    public static <TType, TField> List<TField> listField(Collection<TType> col,
                                                         Function<TType, TField> fieldFetcher,
                                                         Predicate<TType> filter,
                                                         boolean distinct) {
        if (col == null) {
            throw new NullPointerException("The col is null.");
        }
        if (fieldFetcher == null) {
            throw new NullPointerException("The fieldFetcher is null.");
        }
        Stream<TType> stream = col.stream();
        if (filter != null) {
            stream.filter(filter);
        }
        if (distinct) {
            return stream.map(fieldFetcher).distinct().collect(Collectors.toList());
        } else {
            return stream.map(fieldFetcher).collect(Collectors.toList());
        }
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Integer> groupingByInt(Collection<TType> col,
                                                                 Function<TType, TKey> keyFetcher,
                                                                 ToIntFunction<TType> valueFetcher) {
        return groupingByInt(col, keyFetcher, valueFetcher, null);
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Integer> groupingByInt(Collection<TType> col,
                                                                 Function<TType, TKey> keyFetcher,
                                                                 ToIntFunction<TType> valueFetcher,
                                                                 Predicate<TType> filter) {
        if (col == null) {
            throw new NullPointerException("The col is null.");
        }
        if (keyFetcher == null) {
            throw new NullPointerException("The keyFetcher is null.");
        }
        if (valueFetcher == null) {
            throw new NullPointerException("The valueFetcher is null.");
        }
        Stream<TType> stream = col.stream();
        if (filter != null) {
            stream.filter(filter);
        }
        return stream.collect(Collectors.groupingBy(keyFetcher, Collectors.summingInt(valueFetcher)));
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Long> groupingByLong(Collection<TType> col,
                                                               Function<TType, TKey> keyFetcher,
                                                               ToLongFunction<TType> valueFetcher) {
        return groupingByLong(col, keyFetcher, valueFetcher, null);
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Long> groupingByLong(Collection<TType> col,
                                                               Function<TType, TKey> keyFetcher,
                                                               ToLongFunction<TType> valueFetcher,
                                                               Predicate<TType> filter) {
        if (col == null) {
            throw new NullPointerException("The col is null.");
        }
        if (keyFetcher == null) {
            throw new NullPointerException("The keyFetcher is null.");
        }
        if (valueFetcher == null) {
            throw new NullPointerException("The valueFetcher is null.");
        }
        Stream<TType> stream = col.stream();
        if (filter != null) {
            stream.filter(filter);
        }
        return stream.collect(Collectors.groupingBy(keyFetcher, Collectors.summingLong(valueFetcher)));
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Double> groupingByDouble(Collection<TType> col,
                                                                   Function<TType, TKey> keyFetcher,
                                                                   ToDoubleFunction<TType> valueFetcher) {
        return groupingByDouble(col, keyFetcher, valueFetcher, null);
    }

    /**
     * 分组计算集合，按照key-value进行汇总返回。
     */
    public static <TType, TKey> Map<TKey, Double> groupingByDouble(Collection<TType> col,
                                                                   Function<TType, TKey> keyFetcher,
                                                                   ToDoubleFunction<TType> valueFetcher,
                                                                   Predicate<TType> filter) {
        if (col == null) {
            throw new NullPointerException("The col is null.");
        }
        if (keyFetcher == null) {
            throw new NullPointerException("The keyFetcher is null.");
        }
        if (valueFetcher == null) {
            throw new NullPointerException("The valueFetcher is null.");
        }
        Stream<TType> stream = col.stream();
        if (filter != null) {
            stream.filter(filter);
        }
        return stream.collect(Collectors.groupingBy(keyFetcher, Collectors.summingDouble(valueFetcher)));
    }

    /**
     * 判断两个集合是否有交集，如果两个集合中都有值为null的元素，则认为有交集。
     *
     * @param source  源集合
     * @param target  目标集合
     * @param isEqual 判断元素是否相同的方法定义
     * @return true表示有交集，false表示无交集
     */
    public static <T> boolean isIntersected(Collection<T> source, Collection<T> target, BiPredicate<T, T> isEqual) {
        boolean flag = false;
        for (T s : source) {
            for (T t : target) {
                flag = (s == null || t == null) ? (s == null && t == null) : isEqual.test(s, t);
                if (flag) {
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * Map集合值为整形，计算两个具有相同键的差集，并把计算结果赋值给源集合
     *
     * @param sourceMap   源集合
     * @param subtractMap 被减集合
     */
    public static <TKey> void increaseIntegerMap(Map<TKey, Integer> sourceMap, Map<TKey, Integer> subtractMap) {
        if (sourceMap.size() > 0 && subtractMap.size() > 0) {
            for (TKey key : sourceMap.keySet()) {
                int count = sourceMap.get(key);
                int subtractCount = subtractMap.containsKey(key) ? subtractMap.get(key) : 0;
                sourceMap.put(key, count + subtractCount);
            }
        }
    }

    /**
     * Map集合值为整形，计算两个具有相同键的差集，并把计算结果赋值给源集合
     *
     * @param sourceMap   源集合
     * @param subtractMap 被减集合
     */
    public static <TKey> void subtractIntegerMap(Map<TKey, Integer> sourceMap, Map<TKey, Integer> subtractMap) {
        if (sourceMap.size() > 0 && subtractMap.size() > 0) {
            for (TKey key : sourceMap.keySet()) {
                int count = sourceMap.get(key);
                int subtractCount = subtractMap.containsKey(key) ? subtractMap.get(key) : 0;
                sourceMap.put(key, count - subtractCount);
            }
        }
    }

    /**
     * Map集合值为整形，计算两个具有相同键的差集，并把计算结果赋值给源集合
     *
     * @param sourceMap       源集合
     * @param subtractMapList 被减集合
     */
    public static <TKey> void subtractIntegerMap(Map<TKey, Integer> sourceMap, Collection<Map<TKey, Integer>> subtractMapList) {
        if (sourceMap.size() > 0 && subtractMapList.size() > 0) {
            for (TKey key : sourceMap.keySet()) {
                int count = sourceMap.get(key);
                int subtractCount = 0;
                for (Map<TKey, Integer> subtractMap : subtractMapList) {
                    if (subtractMap.size() > 0) {
                        subtractCount += (subtractMap.containsKey(key) ? subtractMap.get(key) : 0);
                    }
                }
                sourceMap.put(key, count - subtractCount);
            }
        }
    }

    /**
     * 把集合划分成若干个子集合，最终子集合个数小于或等于建议的子集合个数
     *
     * @param sourceList 集合
     * @param size       建议的子集合个数
     * @return
     */
    public static <S> List<List<S>> partition(List<S> sourceList, int size) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (size < 1) {
            throw new IndexOutOfBoundsException("size");
        }
        return partitionBy(sourceList, (int) Math.ceil((double) sourceList.size() / size));
    }

    /**
     * 把集合划分成若干个子集合，最终子集合个数小于或等于建议的子集合个数
     *
     * @param sourceList 集合
     * @param size       建议的子集合个数，实际返回的子集合个数小于或等于建议的个数
     * @param factory    生成目标对象的工厂方法
     * @return
     */
    public static <S, T> List<List<T>> partition(List<S> sourceList, int size, Function<S, T> factory) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (size < 1) {
            throw new IndexOutOfBoundsException("size");
        }
        return partitionBy(sourceList, (int) Math.ceil((double) sourceList.size() / size), factory);
    }

    /**
     * 按照包含指定个数的元素来划分集合
     *
     * @param sourceList   集合
     * @param elementCount 子集合包含的指定元素个数
     * @return
     */
    public static <S> List<List<S>> partitionBy(List<S> sourceList, int elementCount) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (elementCount < 1) {
            throw new IndexOutOfBoundsException("elementCount");
        }
        int total = sourceList.size();
        List<List<S>> result = new ArrayList<List<S>>();
        int fromIndex = 0;
        int toIndex = 0;
        while (fromIndex < total) {
            toIndex = (fromIndex + elementCount) > total ? total : fromIndex + elementCount;
            result.add(sourceList.subList(fromIndex, toIndex));
            fromIndex += elementCount;
        }
        return result;
    }

    /**
     * 按照包含指定个数的元素来划分集合
     *
     * @param sourceList   集合
     * @param elementCount 子集合包含的指定元素个数
     * @param factory      生成目标对象的工厂方法
     * @return
     */
    public static <S, T> List<List<T>> partitionBy(List<S> sourceList, int elementCount, Function<S, T> factory) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (elementCount < 1) {
            throw new IndexOutOfBoundsException("elementCount");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }
        int total = sourceList.size();
        List<List<T>> result = new ArrayList<List<T>>();
        int fromIndex = 0;
        int toIndex = 0;
        while (fromIndex < total) {
            toIndex = (fromIndex + elementCount) > total ? total : fromIndex + elementCount;
            result.add(sourceList.subList(fromIndex, toIndex).stream().map(e -> factory.apply(e)).collect(Collectors.toList()));
            fromIndex += elementCount;
        }
        return result;
    }

    /**
     * 把集合划分成若干个子集合
     *
     * @param sourceList 集合
     * @param size       子集合个数
     * @return
     */
    public static <S> List<List<S>> partitionTo(List<S> sourceList, int size) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (size < 1) {
            throw new IndexOutOfBoundsException("size");
        }
        List<List<S>> result = new ArrayList<List<S>>(size);
        int index = 0;
        int total = sourceList.size();
        int maxElementCount = (int) Math.ceil((double) total / size);
        int divide = size - (maxElementCount * size - total);
        int round = 0;
        while (index < total) {
            if (round < divide) {
                result.add(sourceList.subList(index, index + maxElementCount));
                index = index + maxElementCount;
            } else {
                result.add(sourceList.subList(index, index + maxElementCount - 1));
                index = index + maxElementCount - 1;
            }
            round++;
        }
        return result;
    }

    /**
     * 把集合划分成若干个子集合
     *
     * @param sourceList 集合
     * @param size       子集合个数
     * @return
     */
    public static <S, T> List<List<T>> partitionTo(List<S> sourceList, int size, Function<S, T> factory) {
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList");
        }
        if (size < 1) {
            throw new IndexOutOfBoundsException("size");
        }
        List<List<T>> result = new ArrayList<List<T>>(size);
        int index = 0;
        int total = sourceList.size();
        int maxElementCount = (int) Math.ceil((double) total / size);
        int divide = size - (maxElementCount * size - total);
        int round = 0;
        while (index < total) {
            if (round < divide) {
                result.add(sourceList.subList(index, index + maxElementCount).stream().map(e -> factory.apply(e)).collect(Collectors.toList()));
                index = index + maxElementCount;
            } else {
                result.add(sourceList.subList(index, index + maxElementCount - 1).stream().map(e -> factory.apply(e)).collect(Collectors.toList()));
                index = index + maxElementCount - 1;
            }
            round++;
        }
        return result;
    }

    /**
     * 根据第二个集合分支第一个集合，返回三个集合分支，包括同集（相同的元素）、增集（增加的元素）、差集（缺少的元素）
     *
     * @param source     第一个集合
     * @param target     第二个集合
     * @param classifier 映射键值规则
     * @return 返回3个列表集合。
     */
    public static <T, K> List<List<T>> fork(List<T> source, List<T> target, Function<T, K> classifier) {
        if (source == null) {
            throw new IllegalArgumentException("source");
        }
        if (classifier == null) {
            throw new IllegalArgumentException("classifier");
        }
        Map<K, T> map = source.stream().collect(Collectors.toMap(classifier, item -> item));
        List<T> sameList = new ArrayList<T>();
        List<T> addList = new ArrayList<T>();
        List<T> subtractList = new ArrayList<T>();
        if (target != null && target.size() > 0) {
            for (T element : target) {
                K key = classifier.apply(element);
                if (map.containsKey(key)) {
                    sameList.add(element);
                } else {
                    addList.add(element);
                }
                map.remove(key);
            }
        }
        subtractList.addAll(map.values());

        return Arrays.asList(sameList, addList, subtractList);
    }

}
