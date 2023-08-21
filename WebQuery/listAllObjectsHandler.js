'use strict'

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient()
const TableName = "dp-MetadataIndex"

module.exports.listObjects = (event, context, callback) => {
    const params = {
        TableName,
        Select: 'SPECIFIC_ATTRIBUTES',
        AttributesToGet: ['objectID', 'objectURI', 'processingDateTime', 'FILETYPE'],
        Limit: 50
    };
    db.scan(params, (error, result) => {
        if (error) {
            console.error(error)
            callback(null, {
                statusCode: error.statusCode || 501,
                headers: { 'Content-Type': 'text/plain' },
                body: 'Error fetching objects'
            })
            return
        }
        const response = {
            statusCode: 200,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(result.Items)
        }
        callback(null,response)
    })
}