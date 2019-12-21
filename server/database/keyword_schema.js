//keyword schema

var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
	//스키마 정의
	var KeywordSchema = mongoose.Schema({
        email : {type:String},
        keyword : {type:String}
    });
	return KeywordSchema;
};

// module.exports에 ReservationSchema 객체 직접 할당
module.exports = Schema;

