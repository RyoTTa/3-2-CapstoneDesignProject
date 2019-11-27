
module.exports = function(router,fs) {
    console.log('setimage 호출됨.');

    router.route('/set_image').post(function(req,res){
        console.log('/set_image 요청');
        var name = req.body.image_path;
        var data = req.body.data;
        var path = "./images/";
        path=path.concat(name);
        console.log(path);
        console.log(data);
        var buff = Buffer.from(data,'base64');
        fs.open(path,'w',function(err,fd){ 
            if (err){
                res.send('false');
                throw err; 
            }
            console.log('file open complete');
            fs.writeFile(path,buff,function(err){
                if(err)
                    throw err;
                console.log('write end');
                res.send('true');
            });
        });
    });

};
