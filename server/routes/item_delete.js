
var ObjectId = require('mongodb').ObjectId;

module.exports = function(router,app) {
    console.log('item_delete 호출');

    router.route('/item_delete').post(function(req,res){
        console.log('item 삭제 요청');
        var database = app.get('database');
        var _id = req.body._id;
        var email = req.body.executed_user;
        console.log(_id);
        console.log(email);
        database.ItemModel.findOne({'_id':ObjectId(_id)},function(err,item){
            console.log(item.owner_email);
            if(item.owner_email==email)
            {
                database.ItemModel.remove({'_id':ObjectId(_id)},function(err){
                    if(err){
                        res.send('false');
                        throw err;
                    }
                    res.send('true');
                });
            }
        });
       
    });

};
