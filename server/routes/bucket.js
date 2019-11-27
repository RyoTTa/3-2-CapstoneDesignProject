
var ObjectId =require('mongodb').ObjectId;

module.exports = function(router,app) {
    console.log('bucket router 선언');

    router.route('/bucket_insert').post(function(req,res){
        console.log('bucket 등록 요청');
        var database = app.get('database');
        var item_id = req.body.item_id;
        var email = req.body.email;
       
        var bucket = new database.BucketModel({
            'item_id' : item_id,
            'email' : email
        });
        bucket.save(function(err){
                if(err){
                    throw err;
                }
            });
        console.log('bucket 등록');
        res.send('true');
        });
    
    router.route('/bucket_delete').post(function(req,res){
        console.log('bucket 삭제 요청');

        var database = app.get('database');
        var item_id = req.body.item_id;
        var email = req.body.email;

        database.BucketModel.remove({'item_id':item_id,'email':email},function(err,doc){
            if(err){
                throw err;
            }
        });
        res.send('true');
    });
};
