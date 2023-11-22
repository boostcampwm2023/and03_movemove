import { Type, applyDecorators } from '@nestjs/common';
import { ApiExtraModels, ApiResponse, getSchemaPath } from '@nestjs/swagger';

export const ApiSuccessResponse = <TModel extends Type<any>>(option: {
  statusCode?: number;
  description?: string;
  message?: string;
  model: TModel;
}) => {
  return applyDecorators(
    ApiExtraModels(option.model),
    ApiResponse({
      status: option.statusCode ?? 200,
      description: option.description ?? 'OK',
      schema: {
        properties: {
          statusCode: {
            type: 'number',
            example: option.statusCode ?? 200,
          },
          message: {
            type: 'string',
            example: option.message ?? '성공',
          },
          data: {
            $ref: getSchemaPath(option.model),
          },
        },
      },
    }),
  );
};
