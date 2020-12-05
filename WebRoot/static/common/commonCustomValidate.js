jQuery.validator.addMethod("mobile", function(value, element) {
    var length = value.length;
    var expression =  /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
    return this.optional(element) || (length == 11 && expression.test(value));
}, "格式错误");
jQuery.validator.addMethod("idCard", function(value, element) {
    var length = value.length;
    var expression =  /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
    return this.optional(element) || (length == 18 && expression.test(value));
}, "格式错误");