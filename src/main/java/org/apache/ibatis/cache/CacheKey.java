/*
 *    Copyright 2009-2014 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Clinton Begin
 */
/**

 ### CacheKey
 1. MyBatis 对于其 Key 的生成采取规则为：[mappedStementId + offset + limit + SQL + queryParams + environment]生成一个哈希码
 2. MyBatis 中的 CacheKey 类是用来作为缓存键的，用于标识缓存中的唯一对象。CacheKey 的实现方式比较简单，主要包括以下几个关键点：
 3. 构造方法：CacheKey 类的构造方法接收了多个参数，包括 SQL 语句、参数对象、环境 ID 等，这些参数会被用来生成一个唯一的缓存键。
 4. hashCode 方法：CacheKey 类重写了 hashCode 方法，通过将各个参数拼接成一个字符串，并计算其 hashCode 值来生成缓存键的 hashCode。这样可以确保相同的参数组合生成相同的 hashCode。
 5. equals 方法：CacheKey 类重写了 equals 方法，用于比较两个 CacheKey 对象是否相等。在 equals 方法中，会逐个比较各个参数，只有所有参数都相等时才认为两个 CacheKey 对象相等。
 6. toString 方法：CacheKey 类重写了 toString 方法，用于返回 CacheKey 对象的字符串表示形式，方便调试和日志输出。
 7. 使用构造器传入[mappedStementId + offset + limit + SQL + queryParams + environment]
 8. 属性：    hashcode = multiplier（） * hashcode + baseHashCode;  count属性个数，checksum检查的总数，basehashcode
 9. 重写equals方法，==比较内存地址，判断类型，先比hashcode，checksum，count，理论上可以快速比出来，再检查每一项的hashcode的值


 */
public class CacheKey implements Cloneable, Serializable {

  private static final long serialVersionUID = 1146682552656046210L;

  public static final CacheKey NULL_CACHE_KEY = new NullCacheKey();

  private static final int DEFAULT_MULTIPLYER = 37;
  private static final int DEFAULT_HASHCODE = 17;

  private int multiplier;
  private int hashcode;
  private long checksum;
  private int count;
  private List<Object> updateList;

  public CacheKey() {
    this.hashcode = DEFAULT_HASHCODE;
    this.multiplier = DEFAULT_MULTIPLYER;
    this.count = 0;
    this.updateList = new ArrayList<Object>();
  }

  //传入一个Object数组，更新hashcode和效验码
  public CacheKey(Object[] objects) {
    this();
    updateAll(objects);
  }

  public int getUpdateCount() {
    return updateList.size();
  }

  public void update(Object object) {
    if (object != null && object.getClass().isArray()) {
        //如果是数组，则循环调用doUpdate
      int length = Array.getLength(object);
      for (int i = 0; i < length; i++) {
        Object element = Array.get(object, i);
        doUpdate(element);
      }
    } else {
        //否则，doUpdate
      doUpdate(object);
    }
  }

  private void doUpdate(Object object) {
    //计算hash值，校验码
    int baseHashCode = object == null ? 1 : object.hashCode();

    count++;
    checksum += baseHashCode;
    baseHashCode *= count;

    hashcode = multiplier * hashcode + baseHashCode;

    //同时将对象加入列表，这样万一两个CacheKey的hash码碰巧一样，再根据对象严格equals来区分
    updateList.add(object);
  }



  public void updateAll(Object[] objects) {
    for (Object o : objects) {
      update(o);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof CacheKey)) {
      return false;
    }

    final CacheKey cacheKey = (CacheKey) object;

    //先比hashcode，checksum，count，理论上可以快速比出来
    if (hashcode != cacheKey.hashcode) {
      return false;
    }
    if (checksum != cacheKey.checksum) {
      return false;
    }
    if (count != cacheKey.count) {
      return false;
    }

    //万一两个CacheKey的hash码碰巧一样，再根据对象严格equals来区分
    //这里两个list的size没比是否相等，其实前面count相等就已经保证了
    for (int i = 0; i < updateList.size(); i++) {
      Object thisObject = updateList.get(i);
      Object thatObject = cacheKey.updateList.get(i);
      if (thisObject == null) {
        if (thatObject != null) {
          return false;
        }
      } else {
        if (!thisObject.equals(thatObject)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return hashcode;
  }

  @Override
  public String toString() {
    StringBuilder returnValue = new StringBuilder().append(hashcode).append(':').append(checksum);
    for (int i = 0; i < updateList.size(); i++) {
      returnValue.append(':').append(updateList.get(i));
    }

    return returnValue.toString();
  }

  @Override
  public CacheKey clone() throws CloneNotSupportedException {
    CacheKey clonedCacheKey = (CacheKey) super.clone();
    clonedCacheKey.updateList = new ArrayList<Object>(updateList);
    return clonedCacheKey;
  }

}
