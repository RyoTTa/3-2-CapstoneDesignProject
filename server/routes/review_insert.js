
module.exports = function(router,app) {
    console.log('review_insert 호출');

    router.route('/review_insert').post(function(req,res){
        console.log('review 등록 요청');
        var database = app.get('database');
        var review = new database.ReviewModel({
            'star' : req.body.star,
            'contents' : req.body.contents,
            'reviewee' : req.body.reviewee,
            'reviewer' : req.body.reviewer
        });
        review.save(function(err){
            if (err) {
                res.send('false');
                throw err;
            }
            console.log('새로운 review 추가');
        });
        database.UserModel.findOne({ 'email': req.body.reviewee }, function (err, user) {
            user.star_save += req.body.star;
            user.star_count += 1;
            user.save(function (err) {
                if (err) {
                    throw err;
                }
                console.log('user star 갱신');
                res.send('true');
            });
        });

    });

};
