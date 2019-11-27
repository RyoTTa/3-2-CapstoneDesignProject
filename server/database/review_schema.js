
var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
    //스키마 정의
    var ReviewSchema = mongoose.Schema({
        
        star : {type:Number,'default': 0}
        , contents : {type:String,'default':'empty'}
        , reviewee : {type:String,'default':''}
        , reviewer : {type:String,'default':''}
        
    });
	return ReviewSchema;
};

// module.exports에 ItemSchema 객체 직접 할당
module.exports = Schema;