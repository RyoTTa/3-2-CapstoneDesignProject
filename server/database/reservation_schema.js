//reservation schema

var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
	//스키마 정의
	var ReservationSchema = mongoose.Schema({
        item_id : {type:String}
		, owner_email : {type:String,'default':''}
		, borrower_email : {type:String,'default':''}
        , date_start : {type:Date,index: {unique: false},'default':Date.now}
        , date_end : {type:Date,index: {unique: false},'default':Date.now}
    });
	return ReservationSchema;
};

// module.exports에 ReservationSchema 객체 직접 할당
module.exports = Schema;

