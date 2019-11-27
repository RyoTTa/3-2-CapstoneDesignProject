var ObjectId =require('mongodb').ObjectId;

module.exports = function(router,app) {
    console.log('update star 호출됨.');

    router.route('/update_star').post(function(req,res){
        console.log('/update_star요청');
        var star = req.body.star;
        var _id = req.body._id;

        var database = app.get('database');
        
        database.UserModel.findOne({ '_id' :  ObjectId(_id) }, function(err, user) {
            user.star_save +=star;
            user.star_count +=1;
            user.save(function(err){
                if(err){
                    throw err;
                }
                console.log('user star 갱신');
            });
        });
        res.send('star_update완료');
    });
};
