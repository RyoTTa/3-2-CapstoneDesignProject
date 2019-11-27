
var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
    //스키마 정의

	var ItemSchema = mongoose.Schema({
		name : {type:String,'default':''}
		, available_date_start : {type:Date}
		, available_date_end : {type:Date}
		, price_per_date : {type:String,'default':0}
		, latitude:{type:String,'default':''}
		, longitude:{type:String,'default':''}
		, category : {type:String,'default':''}
		, image_path : {type:String,'default':''}
		, owner_email:{type:String,'default':''}
		, owner_phone_number:{type:String,'default':''}
		, contents : {type:String,'default':''}
		, reservation : {type : Boolean, 'defalut' : false}
		, borrower : {type:String,'default':''}
    });
	return ItemSchema;
};

// module.exports에 ItemSchema 객체 직접 할당
module.exports = Schema;

