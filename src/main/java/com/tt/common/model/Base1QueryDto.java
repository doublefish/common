
package com.tt.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 查询参数
 *
 * @author Shuang Yu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Base1QueryDto extends QueryDto<String> {

}
