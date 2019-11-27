//reservation schema

var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
	//스키마 정의
	var BucketSchema = mongoose.Schema({
        email : {type:String},
        item_id : {type:String}
    });
	return BucketSchema;
};

// module.exports에 ReservationSchema 객체 직접 할당
module.exports = Schema;

